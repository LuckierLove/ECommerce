import { defineStore } from "pinia";

export const useThemeStore = defineStore("theme", {
  state: () => ({
    // read persisted preference, default to light
    dark: localStorage.getItem("theme") === "dark",
  }),
  actions: {
    toggle() {
      this.dark = !this.dark;
      localStorage.setItem("theme", this.dark ? "dark" : "light");
      if (this.dark) {
        document.documentElement.classList.add("dark-theme");
        document.documentElement.classList.add("el-theme-dark");
        ensureDarkCss();
      } else {
        document.documentElement.classList.remove("dark-theme");
        document.documentElement.classList.remove("el-theme-dark");
      }
      if (INLINE_FALLBACK) applyInlineTheme(this.dark);
    },
    setDark(v: boolean) {
      this.dark = v;
      localStorage.setItem("theme", v ? "dark" : "light");
      if (v) {
        document.documentElement.classList.add("dark-theme");
        document.documentElement.classList.add("el-theme-dark");
        ensureDarkCss();
      } else {
        document.documentElement.classList.remove("dark-theme");
        document.documentElement.classList.remove("el-theme-dark");
      }
      if (INLINE_FALLBACK) applyInlineTheme(v);
    },
  },
});

// Ensure initial DOM class reflects persisted preference
if (localStorage.getItem("theme") === "dark") {
  document.documentElement.classList.add("dark-theme");
  document.documentElement.classList.add("el-theme-dark");
} else {
  document.documentElement.classList.remove("dark-theme");
  document.documentElement.classList.remove("el-theme-dark");
}

function applyInlineTheme(dark: boolean) {
  // force inline styles on ElementPlus inputs/selects/etc when dark theme is enabled
  const wrappers = Array.from(document.querySelectorAll<HTMLElement>('.el-input__wrapper, .el-select .el-input__wrapper, .el-textarea__wrapper'));
  wrappers.forEach(w => {
    if (dark) {
      w.style.setProperty('background-color', 'rgba(6,10,18,0.88)', 'important');
      w.style.setProperty('border-color', 'rgba(255,255,255,0.06)', 'important');
    } else {
      w.style.removeProperty('background-color');
      w.style.removeProperty('border-color');
    }
  });
  const inners = Array.from(document.querySelectorAll<HTMLElement>('.el-input__inner, .el-select .el-input__inner, .el-textarea__inner'));
  inners.forEach(i => {
    if (dark) {
      i.style.setProperty('background-color', 'transparent', 'important');
      i.style.setProperty('color', '#e6eef8', 'important');
      i.style.setProperty('box-shadow', 'none', 'important');
    } else {
      i.style.removeProperty('background-color');
      i.style.removeProperty('color');
      i.style.removeProperty('box-shadow');
    }
  });
}
// Enable or disable inline fallback. Set to false once global CSS covers all cases.
const INLINE_FALLBACK = false;
// apply inline styles for initial load if enabled
if (INLINE_FALLBACK) {
  const initialDark = localStorage.getItem('theme') === 'dark';
  applyInlineTheme(initialDark);
  setTimeout(()=>applyInlineTheme(initialDark), 500);
}

let _darkCssLoaded = false;
async function ensureDarkCss() {
  if (_darkCssLoaded) return;
  try {
    await import('element-plus/theme-chalk/dark/css-vars.css');
    _darkCssLoaded = true;
  } catch (e) {
    // ignore
  }
}
