import axios from "axios";

import { PRODUCT_API_URL } from "../../../config/constants/secrets.js"; 

class ProductClient {

    async checkProductStock(productsData, token, transactionid) {
        try {
            const headers = {
                Authorization: token,
                transactionid,
            };
            console.info(`Sending request to Product API with data:
                ${JSON.stringify(productsData)} 
                and transactionid: ${transactionid}`
            );
            let response = false;
            
            await axios.post(`${PRODUCT_API_URL}/check-stock`, 
                {products: productsData.products}, { headers })
            .then(res => {
                console.info(`Success response from Product-API. 
                    transactionid: ${transactionid}`
                );
                response = true;
            })
            .catch (err => {
                console.info(`Error response from Product-API. 
                    transactionid: ${transactionid}`
                );
                console.error(err.response.message);
                response = false;
            });
            return response;
        } catch (err) {
            console.info(`Error response from Product-API. 
                transactionid: ${transactionid}`
            );
            return false;
        }
    }
}

export default new ProductClient();

