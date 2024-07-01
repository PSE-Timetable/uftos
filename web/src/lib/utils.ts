import { type ClassValue, clsx } from 'clsx';
import { cubicOut } from 'svelte/easing';
import type { TransitionConfig } from 'svelte/transition';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

type FlyAndScaleParams = {
  y?: number;
  x?: number;
  start?: number;
  duration?: number;
};
export enum WorkingHoursEnum {
  '8:00' = '8:00',
  '8:30' = '8:30',
  '9:00' = '9:00',
  '9:30' = '9:30',
  '10:00' = '10:00',
  '10:30' = '10:30',
  '11:00' = '11:00',
  '11:30' = '11:30',
  '12:00' = '12:00',
  '12:30' = '12:30',
  '13:00' = '13:00',
  '13:30' = '13:30',
  '14:00' = '14:00',
  '14:30' = '14:30',
  '15:00' = '15:00',
  '15:30' = '15:30',
  '16:00' = '16:00',
  '16:30' = '16:30',
  '17:00' = '17:00',
}

export enum WeekdDay {
  'monday' = 'Montag',
  'tuesday' = 'Dienstag',
  'wednesday' = 'Mittwoch',
  'thursday' = 'Donnerstag',
  'friday' = 'Freitag',
  'saturday' = 'Samstag',
  'sunday' = 'Sonntag',
}
export const flyAndScale = (
  node: Element,
  params: FlyAndScaleParams = { y: -8, x: 0, start: 0.95, duration: 150 },
): TransitionConfig => {
  const style = getComputedStyle(node);
  const transform = style.transform === 'none' ? '' : style.transform;

  const scaleConversion = (valueA: number, scaleA: [number, number], scaleB: [number, number]) => {
    const [minA, maxA] = scaleA;
    const [minB, maxB] = scaleB;

    const percentage = (valueA - minA) / (maxA - minA);
    const valueB = percentage * (maxB - minB) + minB;

    return valueB;
  };

  const styleToString = (style: Record<string, number | string | undefined>): string => {
    return Object.keys(style).reduce((str, key) => {
      if (style[key] === undefined) return str;
      return str + `${key}:${style[key]};`;
    }, '');
  };

  return {
    duration: params.duration ?? 200,
    delay: 0,
    css: (t) => {
      const y = scaleConversion(t, [0, 1], [params.y ?? 5, 0]);
      const x = scaleConversion(t, [0, 1], [params.x ?? 0, 0]);
      const scale = scaleConversion(t, [0, 1], [params.start ?? 0.95, 1]);

      return styleToString({
        transform: `${transform} translate3d(${x}px, ${y}px, 0) scale(${scale})`,
        opacity: t,
      });
    },
    easing: cubicOut,
  };
};
