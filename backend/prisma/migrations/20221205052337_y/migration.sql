-- CreateEnum
CREATE TYPE "UserRole" AS ENUM ('APPUSER', 'ADMIN');

-- CreateTable
CREATE TABLE "User" (
    "id" SERIAL NOT NULL,
    "email" TEXT NOT NULL,
    "firstname" TEXT NOT NULL,
    "lastname" TEXT NOT NULL,
    "password" TEXT NOT NULL,
    "phonenumber" INTEGER NOT NULL,

    CONSTRAINT "User_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Trashbin" (
    "id" SERIAL NOT NULL,
    "status" TEXT NOT NULL,
    "authorId" INTEGER NOT NULL,
    "latitude" TEXT NOT NULL,
    "longitude" TEXT NOT NULL,

    CONSTRAINT "Trashbin_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Wastetype" (
    "id" SERIAL NOT NULL,
    "wastetype" TEXT NOT NULL,

    CONSTRAINT "Wastetype_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "WastetypesOnTrashBins" (
    "trashbinId" INTEGER NOT NULL,
    "wastetypeId" INTEGER NOT NULL,
    "assignedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "WastetypesOnTrashBins_pkey" PRIMARY KEY ("trashbinId","wastetypeId")
);

-- CreateIndex
CREATE UNIQUE INDEX "User_email_key" ON "User"("email");

-- AddForeignKey
ALTER TABLE "Trashbin" ADD CONSTRAINT "Trashbin_authorId_fkey" FOREIGN KEY ("authorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WastetypesOnTrashBins" ADD CONSTRAINT "WastetypesOnTrashBins_trashbinId_fkey" FOREIGN KEY ("trashbinId") REFERENCES "Trashbin"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WastetypesOnTrashBins" ADD CONSTRAINT "WastetypesOnTrashBins_wastetypeId_fkey" FOREIGN KEY ("wastetypeId") REFERENCES "Wastetype"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
