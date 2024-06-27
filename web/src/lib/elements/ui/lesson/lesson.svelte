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
  export { title, leftCorner, rightCorner };

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

<div class="flex flex-col w-[200px] {colorClasses[color]} p-4 items-center rounded-lg gap-2">
  <p>{ title }</p>
  <div class="flex flex-row justify-between w-full">
    <p>{ leftCorner }</p>
    <p>{ rightCorner }</p>
  </div>
</div>
