import type { PageLoad } from '../../$types';

export const load = (() => {
  return {
    meta: {
      title: 'Teacher timetable',
    },
  };
}) satisfies PageLoad;
