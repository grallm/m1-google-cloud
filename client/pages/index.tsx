import type { NextPage } from 'next'
import Head from 'next/head'
import Image from 'next/image'
import { Container } from 'react-bootstrap'
import Post from '../components/Post'

const Home: NextPage = () => {
  return (
    <div className='bg-light' style={{ minHeight: '100vh', paddingTop: '57px' }}>
      <Head>
        <title>InstaCrash</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <main className='pt-3 mx-auto' style={{ maxWidth: '720px' }}>
        <Container>
          <Post />
          <Post />
        </Container>
      </main>
    </div>
  )
}

export default Home
