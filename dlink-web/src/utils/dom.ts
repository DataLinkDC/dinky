export function addClassName(el, className) {
  if (el.classList) {
    el.classList.add(className);
  } else {
    if (!el.className.match(new RegExp(`(?:^|\\s)${className}(?!\\S)`))) {
      el.className += ` ${className}`;
    }
  }
}

export function getEventPosition(container = document.body, event) {
  const rect = container.getBoundingClientRect();
  const position = {
    x: event.clientX - rect.left,
    y: event.clientY - rect.top,
  };
  return position;
}

export function removeClassName(el, className) {
  if (el.classList) {
    el.classList.remove(className);
  } else {
    el.className = el.className.replace(
      new RegExp(`(?:^|\\s)${className}(?!\\S)`, 'g'),
      '',
    );
  }
}

export function clearClassName(el) {
  el.className = '';
}

export const on = (target, event, ...args) =>
  target.addEventListener(event, ...args);

export const off = (target, event, ...args) =>
  target.removeEventListener(event, ...args);
