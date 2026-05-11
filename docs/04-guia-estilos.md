# 04. Guía de estilos y prototipado

## 4.1 Prototipo y diseño visual

El diseño de RuleTrack sigue un enfoque utilitario orientado a la gestión documental: jerarquía clara, componentes reutilizables y mínima carga visual para facilitar la lectura de contenido normativo extenso.

El prototipo fue elaborado de forma iterativa, partiendo de wireframes en papel y evolucionando hacia el diseño final implementado directamente en código con Angular y SCSS.

---

## 4.2 Paleta de colores

RuleTrack implementa un sistema de diseño basado en **CSS custom properties** con soporte de tema claro y oscuro (`[data-theme="dark"]`).

### Tema claro (por defecto)

| Token | Valor | Uso |
|---|---|---|
| `--color-primary` | `#FFFFFF` | Fondo general de página |
| `--color-card` | `#FFFFFF` | Fondo de tarjetas e inputs |
| `--color-secondary` | `#BAD2FE` | Bordes, separadores, hover |
| `--color-accent` | `#8AD2FE` | Secciones destacadas, banners |
| `--button-color` | `#5B8DEF` | Botones primarios |
| `--button-hover` | `#3A6BC8` | Hover en botones primarios |
| `--text-button` | `#FFFFFF` | Texto sobre botones |
| `--text-color` | `#333333` | Texto principal |
| `--color-title` | `#7CBDE5` | Títulos de sección |
| `--color-label` | `#5B8DEF` | Etiquetas y headings secundarios |
| `--color-correct` | `#FF0000` | Indicadores de error/corrección |
| `--color-correct-green` | `#1a7a38` | Indicadores de éxito |
| `--color-warning` | `#FFF313` | Alertas y advertencias |

### Badges de visibilidad

| Estado | Fondo | Texto |
|---|---|---|
| PUBLICO | `#d4edda` | `#1a5c3a` |
| SOLO\_MIEMBROS | `#dde4f7` | `#2a3f8a` |
| PRIVADO | `#e2e5ea` | `#3a4252` |

### Gradiente de banner

```
linear-gradient(135deg, #8AD2FE 0%, #5B8DEF 100%)
```

### Tema oscuro (`[data-theme="dark"]`)

| Token | Valor | Uso |
|---|---|---|
| `--color-primary` | `#0F1117` | Fondo de página |
| `--color-card` | `#1A1D27` | Tarjetas e inputs |
| `--color-accent` | `#1E2235` | Secciones, banners |
| `--color-secondary` | `#2A2F45` | Bordes, divisores |
| `--button-color` | `#3A6BC8` | Botones primarios |
| `--text-color` | `#C8D0E0` | Texto principal |
| `--color-title` | `#4A7DE0` | Títulos |
| `--color-correct` | `#F87171` | Error |
| `--color-correct-green` | `#34D399` | Éxito |
| `--color-warning` | `#FBBF24` | Advertencia |

---

## 4.3 Tipografía

La tipografía utiliza una **escala fluida** basada en `clamp()`, lo que garantiza legibilidad en todos los tamaños de pantalla sin necesidad de media queries específicas para texto.

| Token | Rango | Uso |
|---|---|---|
| `--fs-xs` | `0.75rem → 0.875rem` | Textos auxiliares, metadatos |
| `--fs-sm` | `0.8125rem → 0.9375rem` | Etiquetas, notas al pie |
| `--fs-base` | `0.875rem → 1rem` | Cuerpo de texto |
| `--fs-md` | `1rem → 1.125rem` | Párrafos destacados |
| `--fs-lg` | `1.125rem → 1.375rem` | Subtítulos |
| `--fs-xl` | `1.375rem → 1.875rem` | Títulos de sección |
| `--fs-2xl` | `1.875rem → 2.875rem` | Headings principales |

La fuente del sistema es la pila sans-serif nativa del navegador, sin dependencias de fuentes externas, para maximizar rendimiento y privacidad.

---

## 4.4 Espaciado

Sistema de espaciado consistente en 6 niveles:

| Token | Valor | Equivalencia en px |
|---|---|---|
| `--space-xs` | `0.25rem` | 4px |
| `--space-sm` | `0.5rem` | 8px |
| `--space-md` | `1rem` | 16px |
| `--space-lg` | `1.5rem` | 24px |
| `--space-xl` | `2rem` | 32px |
| `--space-2xl` | `3rem` | 48px |

---

## 4.5 Bordes y radios

| Token | Valor | Uso |
|---|---|---|
| `--radius-sm` | `2px` | Inputs pequeños, chips |
| `--radius-md` | `6px` | Cards, botones secundarios |
| `--radius-lg` | `12px` | Modales, paneles |
| `--radius-full` | `9999px` | Badges, avatares circulares |
| `--border-radius` | `2px` | Valor base global |

---

## 4.6 Sombras

| Token | Uso |
|---|---|
| `--shadow-sm` | Elementos sutiles, tooltips |
| `--shadow-md` | Cards en estado normal |
| `--shadow-lg` | Modales, dropdowns |
| `--shadow-btn` | Botones primarios (con tinte azul) |
| `--shadow-card` | Tarjetas de reglamento |

---

## 4.7 Componentes reutilizables

### Card (`card.component`)
Componente genérico de tarjeta usado para mostrar reglamentos en las listas. Aplica `--shadow-card`, `--radius-md` y adaptación al tema activo.

### Modal (`modal.component`)
Componente de diálogo reutilizable con overlay, animación de entrada y cierre por tecla `Escape`. Usado para confirmaciones, edición rápida y formularios.

### Toast Outlet (`toast-outlet.component`)
Sistema de notificaciones no intrusivas. Las notificaciones se apilan verticalmente en la esquina inferior derecha y desaparecen automáticamente. Soporta variantes de éxito, error y advertencia.

### Auth Overlay (`auth-overlay.component`)
Panel lateral o modal de autenticación que se superpone a la vista actual sin perder el contexto de la página. Permite registrarse o iniciar sesión en flujo inline.

---

## 4.8 Estructura de estilos (ITCSS)

Los estilos globales siguen la metodología **ITCSS** (Inverted Triangle CSS):

```
src/styles/
├── settings/      → Variables CSS (custom properties, colores, tipografía)
├── tools/         → Mixins y funciones SCSS
├── generic/       → Reset CSS
├── elements/      → Estilos base de elementos HTML
├── objects/       → Layouts reutilizables (grid, flex containers)
├── components/    → Botones, header, footer
└── utilities/     → Clases de utilidad (helpers)
```

---

## 4.9 Diseño responsive

- Mobile-first: la maquetación base está orientada a pantallas pequeñas.
- Breakpoints progresivos para tablet y escritorio mediante `@media`.
- El sidebar lateral aparece/desaparece según el tamaño de pantalla.
- La escala tipográfica fluida elimina la mayoría de ajustes tipográficos por breakpoint.
