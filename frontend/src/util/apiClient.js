import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  timeout: 10000,
});

/* plan:
        1. you must define all required properties like default headers in axios.create
        2. you must define interceptors to fetch the JWT and refersh tokens if they exist
*/

export default api;