export async function loginUser(credentials) {
    const response = await fetch('http://localhost:8080/WebShopAppREST/rest/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials)
    });
    if (!response.ok) {
        throw new Error('Login failed');
    }
    return await response.json();
}
