import request from './request'
export function getArticles(params) { return request.get('/articles', { params }) }
export function getArticleDetail(id) { return request.get(`/articles/${id}`) }
export function getRandomArticles(n = 3) { return request.get('/articles/random', { params: { n } }) }
export function createArticle(data) { return request.post('/articles', data) }
export function updateArticle(id, data) { return request.put(`/articles/${id}`, data) }
export function deleteArticle(id) { return request.delete(`/articles/${id}`) }
