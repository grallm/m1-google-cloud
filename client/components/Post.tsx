import React, { useState } from 'react'
import { Card } from 'react-bootstrap'
import Image from 'next/image'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHeart } from '@fortawesome/free-regular-svg-icons'
import Link from 'next/link'
import { PostEntity } from '../entities/Post.entity'
import { useSession } from 'next-auth/client'

interface Props {
  post: PostEntity
  showConneSigninAlert: () => void
}
const Post: React.FC<Props> = ({ post, showConneSigninAlert }) => {
  const { ownerId, owner, date, body, url, likes } = post

  const [session] = useSession()

  const likePost = () => {
    if (!session) {
      showConneSigninAlert()
    } else {

    }
  }

  return (
    <Card className="mb-4">
      <Link href={ownerId} passHref>
        <Card.Title role='button' className="p-3 pb-2 d-flex justify-content-between">{owner}<div>{date}</div></Card.Title>
      </Link>

      <Image
        src={url}
        alt="Image"
        layout="responsive"
        width={100}
        height={100}
        objectFit="cover"
        className='border-top border-bottom'
      />

      <Card.Body>
        <div className='d-flex mb-1'>
          <a onClick={() => session && showConneSigninAlert()}><FontAwesomeIcon icon={faHeart} style={{ width: '25px', height: '25px' }} /></a>
          <div className='ms-3'><b>{likes}</b> j'aimes</div>
        </div>

        <Card.Text>
          <Link href={owner} passHref><b role='button'>{owner}</b></Link> {body}
        </Card.Text>
      </Card.Body>
    </Card>
  )
}

export default Post
