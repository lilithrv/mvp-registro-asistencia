const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:3000/api'

export async function api(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    credentials: 'include',
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  })
  if (response.status === 204) return null
  const data = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(data.result || data.message || 'No fue posible completar la solicitud')
  return data
}

export const post = (path, body) => api(path, { method: 'POST', body: JSON.stringify(body || {}) })
export const put = (path, body) => api(path, { method: 'PUT', body: JSON.stringify(body) })
export const remove = (path) => api(path, { method: 'DELETE' })
