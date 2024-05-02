import express from "express";
import {connectMongDb} from "./src/config/db/mongoDbConfig.js";
import {createInitialData} from "./src/config/db/initialData.js";
import { connectRabbitMq } from "./src/config/rabbitmq/rabbitConfig.js"; 
import checkToken from "./src/config/auth/checkToken.js";
import orderRoutes from "./src/modules/sales/routes/OrderRoutes.js";
import tracing from "./src/config/tracing.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8082;
const CONTAINER_ENV = "container";
const THREE_MINUTE = 180000;

starApplication();

async function starApplication() {
    if (CONTAINER_ENV === env.NODE_ENV) {
        console.info("Waiting for RabbitMQ to start.");
        setInterval(() => {
            connectMongDb();        
            connectRabbitMq();
        }, THREE_MINUTE);
    } else {
        connectMongDb();
        createInitialData();
        connectRabbitMq();
    }
}

app.use(express.json());
app.get("/api/initial-data", async (req,res) => {
    await createInitialData();
    return res.json({ message: "Data created." });
})

app.get("/", async (req, res) => {
    return res.status(200).json(getOkResponse())
})

app.get("/api/status", async (req, res) => {
    return res.status(200).json(getOkResponse())
})

function getOkResponse() {
    return {
        service: "Sales-API",
        status: "up",
        httpStatus: 200,
    };
}

app.use(tracing);
app.use(checkToken);
app.use(orderRoutes);


app.listen(PORT, () => {
    console.info(`Server started sucessfully at port ${PORT}`);
})

