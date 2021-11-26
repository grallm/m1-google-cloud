import { useSession, signIn, signOut } from "next-auth/client"
import React from "react"

const Login: React.FC<{}> = () => {
  const [session, loading] = useSession()

  return (
    <div>
      {
        session ? (
          <div>{session}</div>
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