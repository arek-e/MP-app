import express, {Application, Request, Response } from "express";
import {PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

const app: Application = express();
const port = 3000;

//Body parsing Middleware aka plugins till servern
//Creating all functions that the frontend needs
app.use(express.json());
app.use(express.urlencoded({ extended: true}));

//Creating the first endpoint
//make this authenticated so only admin can get all the users
app.get('/users', async (req, res) => {
    const users = await prisma.user.findMany()
    //serializing the data, so we can send the data
    res.json(users)
})
//Getting all the trashbins on the mao
app.get('/trashbins', async(req, res) => {
    const trashbins = await prisma.trashbin.findMany()
    res.json(trashbins)
})

//Getting the trashbins depending on the wastetype
app.get('/trashbin/wastetype/:type', async(req, res) => {
    const trashbinsWithGlass = await prisma.trashbin.findMany({
        where: {
            wastetypes: {
                some: {
                    wastetype: {
                        wastetype: req.params.type
                    }
                }
            }
        }
    })
    res.json(trashbinsWithGlass)
})
//Creating a new user
app.post('/new/user', async(req, res) =>{
    const newUser = await prisma.user.create({
        data:{...req.body},
    })
    res.json(newUser)
})
//creating a new bin
app.post('/new/trashbin', async(req, res) =>{
    const newTrashbin = await prisma.trashbin.create({
        data:{...req.body},
    })
    res.json(newTrashbin)
})

try{
    app.listen(port, (): void => {
        console.log(`Connected successfully on port ${port}`);
    });
} catch (error:any){
    console.error(`Error occurred: ${error.message}`);
}