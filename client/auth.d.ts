// eslint-disable-next-line no-unused-vars
import { Session, DefaultUser } from 'next-auth'

// Add data in session.user type, added in Auth backend
interface UserSession extends DefaultUser {
  accessToken: string
  userId: string
  listFollowing: string[]
}
declare module 'next-auth' {
  // eslint-disable-next-line no-unused-vars
  interface Session {
    user?: UserSession;
  }
}
