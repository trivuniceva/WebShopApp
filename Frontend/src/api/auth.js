export async function loginUser(credentials) {
    const response = await fetch('http://localhost:8080/WebShopAppREST/rest/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials)
    });
    if (!response.ok) {
        const errorMessage = await response.text();
        console.error("Backend error message:", errorMessage);
        throw new Error(errorMessage || 'Login failed');
    }
    return await response.json();
}
