/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    darkMode: "class",
    theme: {
        extend: {
            colors: {
                "primary": "#13ec5b",
                "background-light": "#f6f8f6",
                "background-dark": "#102216",
                "surface-dark": "#112217",
                "surface-light": "#193322",
                "danger": "#ef4444",
                "warning": "#f97316",
            },
            fontFamily: {
                "display": ["Public Sans", "sans-serif"],
                "mono": ["Public Sans", "monospace"]
            },
        },
    },
    plugins: [],
}
