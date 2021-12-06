import { NextPage } from 'next'
import { useEffect, useState } from 'react'
import { getAllUsers } from '../utils/user.api'
import { UserEntity } from '../entities/User.entity'
import { Button, Card, Col, Row } from 'react-bootstrap'
import Link from 'next/link'
import Head from 'next/head'

const Encounter: NextPage = () => {
  const [users, setUsers] = useState<UserEntity[] | null>(null)

  /**
   * Fetch all users
   */
  useEffect(() => {
    getAllUsers()
      .then(users => setUsers(users))
      .catch(() => setUsers([]))
  }, [])

  return (
    <div className='pb-5'>
      <Head>
        <title>InstaCrash - Rencontrer</title>
        <link rel="icon" href="/favicon.ico" />
      </Head>

      <h2>Rencontrer des collègues !</h2>

      <Row xs={3} className='g-2'>
        {
          !users
            ? 'Loading'
            : users.map(({ name, email, listFollowing, id }, i) => (
              <Col key={i}>
                <Card className=' border bg-white'>
                  <Card.Body>
                    <Link href={id} passHref><Card.Title style={{ cursor: 'pointer' }}>{name}</Card.Title></Link>
                    <div>{email}</div>
                    <div><span className=' fw-bold'>{listFollowing?.length || 0}</span> abonnements</div>

                    <Link href={id} passHref><Button variant='outline-primary' size='sm' className='mt-2'>Voir</Button></Link>
                  </Card.Body>
                </Card>
              </Col>
            ))
        }
      </Row>
    </div>
  )
}

export default Encounter
