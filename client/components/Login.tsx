import { useSession, signIn, signOut } from 'next-auth/client'
import React, { useEffect } from 'react'
import { addUser } from '../utils/user.api'

const Login: React.FC<{}> = () => {
  const [session] = useSession()

  useEffect(() => {
    // Add user to Datastore
    if (session) {
      addUser(session)
    }
  }, [session])

  return (
    <div>
      {
        session
          ? (
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
