import { resourceDisplayName, type Resource } from '$lib/components/timetable/timetable';
import type { ComboBoxItem } from '$lib/elements/ui/combo-box/combo-box';
import { getRoomsItems, getStudentGroupsItems, getStudentsItems, getTeachersItems } from '$lib/utils/combobox-items';
import { init } from '$lib/utils/server';
import type { PageLoad } from './$types';

export const load = (async ({ params }) => {
  init();
  const type = params.type as Resource;
  const resources: { [K in Resource]: ComboBoxItem[] } = { class: [], room: [], teacher: [], student: [] };

  switch (type) {
    case 'class': {
      resources[type] = await getStudentGroupsItems({});
      break;
    }
    case 'teacher': {
      resources[type] = await getTeachersItems({});
      break;
    }
    case 'room': {
      resources[type] = await getRoomsItems({});
      break;
    }
    case 'student': {
      resources[type] = await getStudentsItems({});
      break;
    }
  }

  return {
    type,
    id: params.id,
    resources,
    meta: {
      title: `Stundenplan — ${resourceDisplayName[type]}`,
    },
  };
}) satisfies PageLoad;
