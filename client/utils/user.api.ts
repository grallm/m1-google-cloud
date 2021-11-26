import { Session } from "next-auth"
import { apiRoute } from "./common.api"

/**
 * Add a User to the DB
 * @param session session from Google provider
 */
export const addUser = async (session: Session) => {
  await fetch(`${apiRoute}/user`, {
    method: 'POST',
    body: JSON.stringify({
      name: session.user?.name || 'NAME',
      email: session.user?.email || 'EMAIL'
    })
  })
    .catch(e => console.error(e))
}