import Order from "../../modules/sales/model/Order.js";

export async function createInitialData() {
    await Order.collection.drop();
    
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
       updatedAt: new Date()  

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
        },
        status: 'REJECTED',
        createdAt: new Date(),
        updatedAt: new Date()  
 
    });
    let initialData = await Order.find();
    console.info(`initial data was created: ${JSON.stringify(initialData, undefined, 4)}`);

}