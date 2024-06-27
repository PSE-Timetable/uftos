<script context="module" lang="ts">
  export type Color =
    | 'purple'
    | 'orange'
    | 'lightBlue'
    | 'blue'
    | 'green'
    | 'red'
    | 'yellow'
    | 'brown'
    | 'aquamarine';
</script>

<script lang="ts">
  let title: string = 'Deutsch';
  let leftCorner: string = 'Suc';
  let rightCorner: string = '105';
  let length: number = 1;
  export { title, leftCorner, rightCorner, length };

  export let color: Color = 'red';
  const colorClasses: Record<Color, string> = {
    purple: 'bg-subjectPurple',
    red: 'bg-subjectRed',
    orange: 'bg-subjectOrange',
    lightBlue: 'bg-subjectLightBlue',
    blue: 'bg-subjectBlue',
    green: 'bg-subjectGreen',
    yellow: 'bg-subjectYellow',
    brown: 'bg-subjectBrown',
    aquamarine: 'bg-subjectAquamarine',
  };
</script>

<div
  class="row-span-{length} flex flex-col {colorClasses[color]} p-4 items-center rounded-lg gap-2 justify-between w-full h-full">
  <p>{ title }</p>
  <div class="flex flex-row justify-between w-full gap-6">
    <p>{ leftCorner }</p>
    <p>{ rightCorner }</p>
  </div>
</div>
