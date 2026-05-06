import { Component, Input, Output, EventEmitter, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: true,
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss',
  encapsulation: ViewEncapsulation.Emulated,
})
export class ModalComponent {
  @Input() visible = false;
  @Input() titulo = '';
  @Output() formatSelected = new EventEmitter<'md' | 'pdf'>();
  @Output() cancelled = new EventEmitter<void>();

  selectFormat(format: 'md' | 'pdf'): void {
    this.formatSelected.emit(format);
  }

  cancel(): void {
    this.cancelled.emit();
  }
}
