import request from './request'
export function getComments(articleId) { return request.get(`/articles/${articleId}/comments`) }
export function createComment(articleId, data) { return request.post(`/articles/${articleId}/comments`, null, { params: data }) }
export function deleteComment(id) { return request.delete(`/comments/${id}`) }
