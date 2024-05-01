import mongoose from "mongoose";

import {MONGO_DB_URL} from "../constants/secrets.js";

export function connectMongDb() {
    mongoose.connect(MONGO_DB_URL, {
        useNewUrlParser: true,
        serverSelectionTimeoutMS: 180000,
    })

    mongoose.connection.on("connected", function() {
        console.info('Application connected to MongoDb successfully.');
    })
    mongoose.connection.on("error", function() {
        console.error('Application connected to MongoDb successfully.');
    })
}