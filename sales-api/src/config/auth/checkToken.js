import jwt from "jsonwebtoken";
import { promisify } from "util";
import AuthException from "./AuthException.js";
import { API_SECRET } from "../constants/secrets.js";
import { UNAUTHORIZED, INTERNAL_SERVER_ERROR } from "../constants/httpStatus.js";

const bearer = "bearer ";
const emptyString = " ";

export default async (req, res, next) => {
    
    try {
        const { authorization } = req.headers;
        if (!authorization) {
            throw new AuthException(UNAUTHORIZED, "Access Token was not informed.");
        }
        
        let accessToken = authorization;
        if (accessToken.includes(emptyString)) {
            accessToken = accessToken.split(emptyString)[1];
        }

        const decoded = await promisify(jwt.verify)(accessToken, API_SECRET);
        req.authUser = decoded.authUser
        return next();

    } catch (err) {
        const status = err.status ? err.status : INTERNAL_SERVER_ERROR;
        return res.status(status).json({
            status,
            message: err.message,
        })
    }
};
