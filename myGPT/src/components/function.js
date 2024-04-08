export function getCookie(name) {
    console.log(document.cookie);
    const cookie = document.cookie
        .split(';')
        .find((cookie) => cookie.trim().startsWith(name + '='));
    return cookie ? cookie.split('=')[1] : '';
}