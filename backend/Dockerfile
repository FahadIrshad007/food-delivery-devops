# Use Node.js 18 as the base image
FROM node:18
# Set the working directory
WORKDIR /app
# Copy package files and install dependencies
COPY package*.json ./
RUN npm install
# Copy the rest of the backend code
COPY . .
# Expose the backend port (check your server.js, usually 4000)
EXPOSE 4000
# Start the server
CMD ["npm", "start"]