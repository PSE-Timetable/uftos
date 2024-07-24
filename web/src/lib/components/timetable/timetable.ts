import type { Color, Slot } from '../lesson/lesson';

export type TimetableItems = Array<Array<LessonItem>>;

export type LessonItem = {
  length: number;
  color?: Color;
  title: Slot;
  bottomLeft: Slot;
  bottomRight: Slot;
  lessonId: string;
} | null;

export const getTimetableMatrix = (slots: number, days: number) => {
  const result: TimetableItems = Array.from({ length: slots - 1 });
  for (let i = 0; i < slots; i++) {
    result[i] = Array.from<LessonItem>({ length: days }).fill(null);
  }
  return result;
};

export const mergeLessons = ((timetable: TimetableItems) => {
  const result: TimetableItems = [...timetable];
  for (let i = 0; i < result[0].length; i++) {
    for (let j = 0; j < result.length - 1; j++) {
      // for some reason this causes the result type to be TimetableItems | undefined
      if (result[j][i] === null) {
        continue;
      }

      let k = j + 1;

      while (k < result.length && result[j][i]?.lessonId === result[k][i]?.lessonId) {
        result[j][i]!.length++;
        result[k][i] = null;
        k++;
      }
      j = k - 1;
    }

    return result;
  }
}) as (timetable: TimetableItems) => TimetableItems;
