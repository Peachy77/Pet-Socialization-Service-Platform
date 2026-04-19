import axios from "axios";

const apiClient = axios.create({
  baseURL: "http://localhost:8080",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json"
  }
});

// 请求拦截器：自动带 token
apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    // 统一转换：后端 code=1 表示成功，前端期望 code=0
    if (response.data && response.data.code === 1) {
      response.data.code = 0;
    }
    return response.data;
  },
  error => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

// // 响应拦截器
// apiClient.interceptors.response.use(
//   response => response.data,
//   error => {
//     console.error("API Error:", error);
//     return Promise.reject(error);
//   }
// );

export default apiClient;