import { useSession, signIn, signOut } from "next-auth/client"
import React, { useEffect } from "react"

const apiRoute = process.env.NEXT_PUBLIC_API_URL || ''

const Login: React.FC<{}> = () => {
  const [session] = useSession()

  useEffect(() => {
    // Add user to Datastore
    if (session) {
      fetch(`${apiRoute}/user`, {
        method: 'POST',
        body: JSON.stringify({
          name: 'Malo',
          email: 'malo.grall@gmail.com'
        })
      })
      .catch(e => console.error(e))
    }
  }, [session])

  return (
    <div>
      {
        session ? (
          <div>
            {session.user?.email}
            {session.user?.name}
            <button onClick={() => signOut()}>Sign out</button>
          </div>
        )
        : (
          <div>
            Not signed in <br />
            <button onClick={() => signIn('google', { callbackUrl: '/' })}>Sign in</button>
          </div>
        )
      }
    </div>
  )
}

export default Login