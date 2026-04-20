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
    // 后端如果直接从 Authorization 读取 JWT，通常需要原始 token 而不是 Bearer 前缀
    config.headers.Authorization = "Bearer " + token;
    // 兼容部分后端拦截器只读取 token 请求头的场景
    config.headers.token = token;
  }

  return config;
});

// 响应拦截器
apiClient.interceptors.response.use(
  response => response.data,
  error => {
    console.error("API Error:", error);
    return Promise.reject(error);
  }
);

export default apiClient;