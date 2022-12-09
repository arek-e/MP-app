/*
  Warnings:

  - You are about to drop the column `name` on the `User` table. All the data in the column will be lost.
  - You are about to drop the `Post` table. If the table is not empty, all the data it contains will be lost.
  - Added the required column `firstname` to the `User` table without a default value. This is not possible if the table is not empty.
  - Added the required column `lastname` to the `User` table without a default value. This is not possible if the table is not empty.
  - Added the required column `password` to the `User` table without a default value. This is not possible if the table is not empty.
  - Added the required column `phonenumber` to the `User` table without a default value. This is not possible if the table is not empty.

*/
-- DropForeignKey
ALTER TABLE "Post" DROP CONSTRAINT "Post_authorId_fkey";

-- AlterTable
ALTER TABLE "User" DROP COLUMN "name",
ADD COLUMN     "firstname" TEXT NOT NULL,
ADD COLUMN     "lastname" TEXT NOT NULL,
ADD COLUMN     "password" TEXT NOT NULL,
ADD COLUMN     "phonenumber" INTEGER NOT NULL;

-- DropTable
DROP TABLE "Post";

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

-- AddForeignKey
ALTER TABLE "Trashbin" ADD CONSTRAINT "Trashbin_authorId_fkey" FOREIGN KEY ("authorId") REFERENCES "User"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WastetypesOnTrashBins" ADD CONSTRAINT "WastetypesOnTrashBins_trashbinId_fkey" FOREIGN KEY ("trashbinId") REFERENCES "Trashbin"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "WastetypesOnTrashBins" ADD CONSTRAINT "WastetypesOnTrashBins_wastetypeId_fkey" FOREIGN KEY ("wastetypeId") REFERENCES "Wastetype"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
