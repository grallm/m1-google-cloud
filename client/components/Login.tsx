import { useSession, signIn, signOut } from "next-auth/client"
import React from "react"

const Login: React.FC<{}> = () => {
  const [session, loading] = useSession()

  console.log(session)

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