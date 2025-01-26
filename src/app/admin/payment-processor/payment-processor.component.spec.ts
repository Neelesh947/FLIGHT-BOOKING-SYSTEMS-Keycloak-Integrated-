import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentProcessorComponent } from './payment-processor.component';

describe('PaymentProcessorComponent', () => {
  let component: PaymentProcessorComponent;
  let fixture: ComponentFixture<PaymentProcessorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaymentProcessorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PaymentProcessorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
