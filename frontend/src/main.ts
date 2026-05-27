import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import "element-plus/dist/index.css";
import "./styles/base.css";
import App from "./App.vue";
import router from "./router";

async function bootstrap() {
	// Apply persisted theme class before mounting so CSS variables load correctly
	const initialTheme = localStorage.getItem("theme");
	if (initialTheme === "dark") {
		document.documentElement.classList.add("dark-theme");
		document.documentElement.classList.add("el-theme-dark");
		// load ElementPlus dark variables to override defaults
		await import("element-plus/theme-chalk/dark/css-vars.css");
	}

	const app = createApp(App);
	app.use(createPinia());
	app.use(router);
	app.use(ElementPlus, { locale: zhCn });

	app.mount("#app");
}

bootstrap();
