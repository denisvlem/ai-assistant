document.addEventListener("DOMContentLoaded", function () {
    const sendButton = document.getElementById("send-message-btn");
    const chatInput = document.getElementById("chat-input");
    const messageHistoryContainer = document.getElementById("message-history");


    sendButton.addEventListener("click", function () {
        console.info("Send message is pushed");
        const prompt = chatInput.value;
        if (!prompt) return;

        chatInput.value = "";

        const newUserMessage = document.createElement("div");
        newUserMessage.innerHTML = `<div class="chat-bubble user">${prompt}</div>`;
        messageHistoryContainer.appendChild(newUserMessage);

        const urlParts = window.location.pathname.split("/");
        const chatId = urlParts[urlParts.length - 1];
        const url = `/chat/${chatId}/ask-with-stream?prompt=${encodeURIComponent(prompt)}`;

        const responseStreamEventSource = new EventSource(url);
        let fullText = "";

        const aiResponse = document.createElement("div");
        messageHistoryContainer.appendChild(aiResponse);

        responseStreamEventSource.onmessage = function (event) {
            const data = JSON.parse(event.data);
            let token = data.text;
            console.log(token);
            fullText += token;
            aiResponse.innerHTML = `<div class="chat-bubble ai">${marked.parse(fullText)}</div>`;
            messageHistoryContainer.scrollTop = messageHistoryContainer.scrollHeight;
        };

        responseStreamEventSource.onerror = function (e) {
            console.error("Ошибка SSE:", e);
            responseStreamEventSource.close();
        };
    })
})