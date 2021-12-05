export interface UserEntity {
  id: string
  email: string
  name: string
  lastConnected: string
  listFollowing: [string] | null
}
