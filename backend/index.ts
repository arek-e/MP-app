import express, {Application, Request, Response } from "express";
import {PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

const app: Application = express();
const port = 3000;

//Body parsing Middleware aka plugins till servern
app.use(express.json());
app.use(express.urlencoded({ extended: true}));

app.get(
    "/",
    async (req: Request, res: Response): Promise<Response> => {
        return res.status(200).send({
            message: "Hello World!"
        });
    }
);
app.get('/feed', async (req: Request, res: Response) => {
    const posts = await prisma.post.findMany({
        where: { published: true},
        include: {author: true},
    })
    res.json(posts)
});

app.post('/post', async (req: Request, res: Response) => {
const { title, content, authorEmail } = req.body
const result = await prisma.post.create({
    data: {
        title,
        content,
        published: false,
        author: { connect: { email: authorEmail } },
    },
})
res.json(result)
});
app.delete('/post/:id', async (req: Request, res: Response) => {
    const { id } = req.params
    const post = await prisma.post.delete({
        where: {
            id: Number(id),
        },
    })
    res.json(post)
})
app.put('/publish/:id', async (req: Request, res: Response) => {
    const { id } = req.params
    const post = await prisma.post.update({
        where: { id: Number(id) },
        data: { published: true },
    })
    res.json(post)
})

try{
    app.listen(port, (): void => {
        console.log(`Connected successfully on port ${port}`);
    });
} catch (error:any){
    console.error(`Error occurred: ${error.message}`);
}