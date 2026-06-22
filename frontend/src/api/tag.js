import request from './request'
export function getTags() { return request.get('/tags') }
export function getTagNames() { return request.get('/tags/names') }
