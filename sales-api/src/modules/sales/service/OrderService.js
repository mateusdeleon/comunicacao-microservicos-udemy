import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/productStockUpdateSender.js";
import { ACCEPTED, REJECTED, PENDING } from "../status/OrderStatus.js";
import OrderException from "../exception/OrderException.js";
import { BAD_REQUEST } from "../../../config/constants/httpStatus.js";
import ProductClient from "../../product/client/ProductClient.js";

class OrderService {

    async createOrder(req) {
        try {
            let orderData = req.body;
            const {transactionid, serviceid} = req.headers;
            console.info(
                `Request to POST new order with data ${JSON.stringify(orderData)} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );

            this.validateOrderData(orderData);
            const { authUser } = req;
            const { authorization } = req.headers;
            let order = this.createInitialOrderData(orderData, authUser, transactionid, serviceid);
            await this.validateProductStock(order, authorization, transactionid);
            let createdOrder = await OrderRepository.save(order);
            this.sendMessage(createdOrder, transactionid);

            let response = {
                status: httpStatus.SUCCESS,
                createdOrder,
            };
            console.info(
                `Response to POST login with data ${JSON.stringify(response)} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            return response;
            
        } catch (err) {
            return {
                status: err.status ? err.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: err.message,
            };
        }
    }

    createInitialOrderData(orderData, authUser, transactionid, serviceid) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            transactionid,
            serviceid,
            products: orderData.prodcuts,
        };
    }

    async updateOrder(orderMessage) {
        try {
            const order = JSON.parse(orderMessage);
            if (order.salesId && order.status) {
                let existingOrder = await OrderRepository.findById(order.salesId);
                if (existingOrder && order.status !== existingOrder.status) {
                    existingOrder.status = order.status;
                    existingOrder.updatedAt = new Date();
                    await OrderRepository.save(existingOrder);
                }
            } else {
                console.warn(
                    `The order message was not complete. 
                    transactionId: ${orderMessage.transactionid}`
                );
            }
            
        } catch (err) {
            console.error(
                BAD_REQUEST, 
                "Coud not parse order message from queue."
            );
            console.error(err.message);
        }
    }

    validateOrderData(data) {
        if (!data || !data.products) {
            throw new OrderException(
                BAD_REQUEST, 
                'The products must be informed.'
            )
        }
    }

    async validateProductStock(order, token, transactionid) {
        let stockIsOk = await ProductClient.checkProductStock(order, token, 
            transactionid);

        if (!stockIsOk) {
            throw new OrderException(
                BAD_REQUEST, 
                "The stock is out for the products."
            )
        }
    }

    sendMessage(createdOrder, transactionid) {
        const message = {
            salesId: createdOrder.id,
            prodcuts: createdOrder.prodcuts,
            transactionid,
        }
        sendMessageToProductStockUpdateQueue(message);
    }

    async findById(req) {
        try {
            const {id} = req.params;
            const {transactionid, serviceid} = req.headers;
            console.info(
                `Request to GET order by id ${id} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            this.validateInformedId(id);
            const existingOrder = await OrderRepository.findById(id);
            if (!existingOrder) {
                throw new OrderException(
                    BAD_REQUEST, 
                    "Order was not found."
                )
            }

            let response = {
                status: httpStatus.SUCCESS,
                existingOrder,
            };
            console.info(
                `Response to GET order by id ${id}: ${JSON.stringify(response)} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            return response;
            
        } catch (err) {
            return {
                status: err.status ? err.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: err.message,
            }
        }        
    }

    async findByAll() {
        try {
            const {transactionid, serviceid} = req.headers;
            console.info(
                `Request to GET all orders | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            
            const orders = await OrderRepository.findAll();
            if (!orders) {
                throw new OrderException(
                    BAD_REQUEST, 
                    "No Orders were found."
                )
            }

            let response = {
                status: httpStatus.SUCCESS,
                orders,
            };
            console.info(
                `Response to GET all orders ${JSON.stringify(response)} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            return response;
            
        } catch (err) {
            return {
                status: err.status ? err.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: err.message,
            }
        }        
    }

    async findByProductId(req) {
        try {
            const {productId} = req.params;
            const {transactionid, serviceid} = req.headers;
            console.info(
                `Request to GET orders by productid: ${productId} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            this.validateInformedId(productId);
            const orders = await OrderRepository.findByProductId(productId);
            if (!orders) {
                throw new OrderException(BAD_REQUEST, "Order was not found.")
            }

            let response = {
                status: httpStatus.SUCCESS,
                salesIds: orders.map((order) => {return order.id}),
            };
            console.info(
                `Response to GET orders by productid: ${productId}: 
                ${JSON.stringify(response)} | 
                [transactionid: ${transactionid} | serviceid: ${serviceid}]`
            );
            return response;
            
        } catch (err) {
            return {
                status: err.status ? err.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: err.message,
            }
        }        
    }

    validateInformedId(id) {
        if (!id) {
            throw new OrderException(BAD_REQUEST, "The id must be informed.");
        }
    }

}

export default new OrderService();
