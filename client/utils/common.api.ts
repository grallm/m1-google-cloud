export const apiRoute = process.env.NEXT_PUBLIC_API_URL || ''

export interface EntityList<E> {
  items: E[]
}
