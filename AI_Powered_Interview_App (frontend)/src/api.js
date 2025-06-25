import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8088/api/prep',
  withCredentials: true
});

export default api;
