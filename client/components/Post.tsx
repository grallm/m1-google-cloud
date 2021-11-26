import React from "react"
import { Button, Card } from "react-bootstrap"
import Image from 'next/image'

interface Props {
  
}
const Post: React.FC<{}> = () => {
  return (
    <Card className="mb-4">
      <Card.Body>
        <div>
          <Image
            src='https://img.20mn.fr/sIChN5W-TCG0VWSpGYJYLw/768x492_tous-trolls.jpg'
            alt="Image"
            layout="responsive"
            width={100}
            height={100}
            objectFit="cover"
          />
        </div>
        <Card.Title>Card Title</Card.Title>
        <Card.Text>
          Some quick example text to build on the card title and make up the bulk of
          the card&apos;s content.
        </Card.Text>
        <Button variant="primary">Go somewhere</Button>
      </Card.Body>
    </Card>
  )
}

export default Post