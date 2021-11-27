import React from 'react'
import { Button, Card } from 'react-bootstrap'
import Image from 'next/image'

interface Props {
  owner: string
  image: string
  description: string
}
const Post: React.FC<Props> = ({ image, owner, description }) => {
  return (
    <Card className="mb-4">
      <Card.Title className="p-3">{owner}</Card.Title>

      <Image
        src={image}
        alt="Image"
        layout="responsive"
        width={100}
        height={100}
        objectFit="cover"
      />

      <Card.Body>
        <Card.Text>{description}</Card.Text>
        <Button variant="primary">Go somewhere</Button>
      </Card.Body>
    </Card>
  )
}

export default Post
