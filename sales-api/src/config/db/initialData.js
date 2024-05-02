import { v4 as uuidv4 } from "uuid";
import Order from "../../modules/sales/model/Order.js";

export async function createInitialData() {
    
    try {
        
        let existingData = await Order.find();
        if (existingData && existingData.length > 0) {
          console.info("Remove existing data...");
          await Order.collection.drop();
        }
    
        await Order.create({
        products: [
                {
                    productId: 1,
                    quantity: 3
                },
                {
                    productId: 2,
                    quantity: 3
                },
                {
                    productId: 3,
                    quantity: 3
                }
        ],
        user: {
                id: 'aasdasd787987',
                name: 'usuarioTeste',
                email: 'usuarioteste@gmail.com' 
        },
        status: 'APPROVED',
        createdAt: new Date(),
        updatedAt: new Date(),
        transactionid: uuidv4(),  
        serviceid: uuidv4(),
        });

        await Order.create({
            products: [
                {
                    productId: 4,
                    quantity: 2
                },
                {
                    productId: 5,
                    quantity: 2
                },
                {
                    productId: 6,
                    quantity: 2
                }
            ],
            user: {
                id: 'aasdasd787987',
                name: 'usuarioTeste',
                email: 'usuarioteste@gmail.com' 
            },transactionid: uuidv4(),  
            serviceid: uuidv4(),
            status: 'REJECTED',
            createdAt: new Date(),
            updatedAt: new Date(),
            transactionid: uuidv4(),  
            serviceid: uuidv4(),  
        });
        
        let initialData = await Order.find();
        console.info(`initial data was created: ${JSON.stringify(initialData, undefined, 4)}`);
    } catch (error) {
        console.error(error);
    }

}