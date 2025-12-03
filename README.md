# Pakhom Bot
https://t.me/pakhom_bot

Crazy multifunctional bot for telegram.


This bot has a lot of features:
* Meme generation: you can send photo and text and bot will generate meme for it
* Scripting: a very heavy and multifunctional engine which allows you to write your custom scenarios (scripts) for bot's behaviour in the chat in real time and without rebuild. 
For example you can write script to remove all messages with word `F*ck` in the chat, or you can ask bot to call some REST API and post message with result to the chat. Use command `/help` for more information. 
* Instagram/Youtube/Twitter posting to chat according on subscriptions
* Some another minor features like random meme generation and notifying all participants in the chat by @all.

I didn't try to write beautiful code so... it looks like a shit :)

## CI/CD

The repository now contains the workflow `.github/workflows/portainer-deploy.yml` that builds the
application, publishes an image to GitHub Container Registry and triggers a Portainer stack
redeploy. Configure the following secrets before enabling the workflow:

- `PORTAINER_URL` – base URL of the Portainer instance (for example `https://portainer.example.com`).
- `PORTAINER_USERNAME` / `PORTAINER_PASSWORD` – credentials with rights to deploy the target stack.
- `PORTAINER_STACK_ID` – identifier of the stack to redeploy.
