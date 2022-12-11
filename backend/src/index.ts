import { PrismaClient } from "@prisma/client";
import express from "express";

const prisma = new PrismaClient();

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use(express.raw({ type: "application/vnd.custom-type" }));
app.use(express.text({ type: "text/html" }));

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
  const type = req.params.type
  const trashbinsWithGlass = await prisma.trashbin.findMany({
    where: {
      wastetypes: {
        some: {
          wastetype: {
            wastetype: type
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

app.get("/", async (req, res) => {
  res.send(
    `
  <h1>Binhunter REST API</h1>
  <h1>Not updated currently</h1>
  <h2>Available Routes</h2>
  <pre>
    GET, POST /todos
    GET, PUT, DELETE /todos/:id
  </pre>
  `.trim(),
  );
});

app.listen(port, () => {
  console.log(`Example app listening at http://localhost:${port}`);
});
