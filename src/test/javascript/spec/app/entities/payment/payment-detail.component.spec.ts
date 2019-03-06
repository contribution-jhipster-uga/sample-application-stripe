/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StripeTestModule } from '../../../test.module';
import { PaymentDetailComponent } from 'app/entities/payment/payment-detail.component';
import { Payment } from 'app/shared/model/payment.model';

describe('Component Tests', () => {
    describe('Payment Management Detail Component', () => {
        let comp: PaymentDetailComponent;
        let fixture: ComponentFixture<PaymentDetailComponent>;
        const route = ({ data: of({ payment: new Payment(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [StripeTestModule],
                declarations: [PaymentDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(PaymentDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(PaymentDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.payment).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
