import type { NextPage } from 'next'
import { useRouter } from 'next/dist/client/router'
import Head from 'next/head'
import { Container } from 'react-bootstrap'

const User: NextPage = () => {
  const router = useRouter()
  const { user } = router.query

  return (
    <Container className=' bg-white border p-3 rounded'>
      <Head>
        <title>InstaCrash - {user}</title>
      </Head>

      <h3 className='mb-3'>{user}</h3>
    </Container>
  )
}

export default User
