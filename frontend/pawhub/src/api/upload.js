// api/upload.js
import apiClient from "./client"

export function uploadFile(file) {
  const formData = new FormData()
  formData.append("file", file)

  return apiClient.post("/upload", formData, {
    headers: {
      "Content-Type": "multipart/form-data"
    }
  })
}