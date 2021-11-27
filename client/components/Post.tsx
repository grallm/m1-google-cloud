import React from 'react'
import { Button, Card } from 'react-bootstrap'
import Image from 'next/image'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faHeart } from '@fortawesome/free-regular-svg-icons'

interface Props {
  owner: string
  image: string
  description: string
}
const Post: React.FC<Props> = ({ image, owner, description }) => {
  return (
    <Card className="mb-4">
      <Card.Title className="p-3 pb-2">{owner}</Card.Title>

      <Image
        src={image}
        alt="Image"
        layout="responsive"
        width={100}
        height={100}
        objectFit="cover"
        className='border-top border-bottom'
      />

      <Card.Body>
        <div className='d-flex mb-1'>
          <a><FontAwesomeIcon icon={faHeart} style={{ width: '25px', height: '25px' }} /></a>
          <div className='ms-3'><b>123</b> j'aimes</div>
        </div>
        <Card.Text><b>{owner}</b> {description}</Card.Text>
      </Card.Body>
    </Card>
  )
}

export default Post
