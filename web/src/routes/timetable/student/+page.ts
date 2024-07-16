import type { PageLoad } from '../../$types';

export const load = (() => {
  return {
    meta: {
      title: 'Student timetable',
    },
  };
}) satisfies PageLoad;
