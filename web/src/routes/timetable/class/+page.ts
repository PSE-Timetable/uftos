import type { PageLoad } from '../../$types';

export const load = (() => {
  return {
    meta: {
      title: 'Class timetable',
    },
  };
}) satisfies PageLoad;
