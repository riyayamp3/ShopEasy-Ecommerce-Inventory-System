-- Run this to add image_url column to COMPLAINT table
ALTER TABLE COMPLAINT ADD COLUMN IF NOT EXISTS image_url TEXT;
